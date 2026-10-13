package com.blue.apartamentos.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blue.apartamentos.models.DisponibilidadModel;
import com.blue.apartamentos.models.PagoModel;
import com.blue.apartamentos.models.ReservacionModel;
import com.blue.apartamentos.models.UnidadRentableModel;
import com.blue.apartamentos.repositories.IDisponibilidadRepository;
import com.blue.apartamentos.repositories.IPagoRepository;
import com.blue.apartamentos.repositories.IReservacionRepository;
import com.blue.apartamentos.repositories.IUnidadRentableRepository;
import com.blue.apartamentos.repositories.IUsuarioRepository;

// Lógica de negocio para reservaciones: valida disponibilidad, detecta traslapes y calcula el precio total
@Service
public class ReservacionService {
    @Autowired
    private IReservacionRepository reservacionRepository;
    @Autowired
    private IDisponibilidadRepository disponibilidadRepository; // para verificar periodos de disponibilidad
    @Autowired
    private IUnidadRentableRepository unidadRentableRepository;
    @Autowired
    private IUsuarioRepository usuarioRepository;
    @Autowired
    private IPagoRepository pagoRepository; // solo se usa en guardarReservacionConPago

    // Retorna todas las reservaciones del sistema
    public ArrayList<ReservacionModel> obtenerReservaciones() { return (ArrayList<ReservacionModel>) reservacionRepository.findAll(); }

    // Guarda una nueva reservación; valida disponibilidad, capacidad, traslapes y calcula el precio
    @Transactional
    public ReservacionModel guardarReservacion(ReservacionModel reservacion) {
        prepararReservacion(reservacion, null);
        return reservacionRepository.save(reservacion);
    }

    // Guarda una reservación con pago inicial en la misma transacción; confirma si el pago es aceptado
    @Transactional
    public ReservacionModel guardarReservacionConPago(ReservacionModel reservacion, PagoModel pagoInicial) {
        if (pagoInicial == null) {
            throw new IllegalArgumentException("El pago inicial es obligatorio");
        }

        prepararReservacion(reservacion, null);
        validarMontoInicial(pagoInicial, reservacion.getPrecioTotal());

        if (pagoInicial.getEstado() == PagoModel.Estado.aceptado) {
            reservacion.setEstado(ReservacionModel.Estado.confirmada);
        } else {
            reservacion.setEstado(ReservacionModel.Estado.pendiente);
        }

        ReservacionModel guardada = reservacionRepository.save(reservacion);
        pagoInicial.setReservacion(guardada);
        pagoRepository.save(pagoInicial);
        return guardada;
    }

    // Busca una reservación por ID; retorna Optional vacío si no existe
    public Optional<ReservacionModel> obtenerPorId(Long id) { return reservacionRepository.findById(id); }

    // Actualiza fechas, huéspedes, notas, estado (con validación) y comentario de la reservación
    public ReservacionModel actualizarReservacion(ReservacionModel request, Long id) {
        ReservacionModel reservacion = reservacionRepository.findById(id).orElseThrow(() -> new RuntimeException("Reservacion no encontrada con el ID: " + id));
        reservacion.setFechaEntrada(request.getFechaEntrada());
        reservacion.setFechaSalida(request.getFechaSalida());
        reservacion.setFechaCheckin(request.getFechaCheckin());
        reservacion.setFechaCheckout(request.getFechaCheckout());
        reservacion.setNumeroHuespedes(request.getNumeroHuespedes());
        reservacion.setNotas(request.getNotas());
        if (request.getEstado() != null) {
            validarCambioEstado(reservacion, request.getEstado());
            reservacion.setEstado(request.getEstado());
        }
        reservacion.setComentario(request.getComentario());
        prepararReservacion(reservacion, id);
        return reservacionRepository.save(reservacion);
    }

    // Cambia el estado de una reservación siguiendo la máquina de estados definida
    @Transactional
    public ReservacionModel cambiarEstadoReservacion(Long id, ReservacionModel.Estado nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado es obligatorio");
        }
        ReservacionModel reservacion = reservacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservacion no encontrada con el ID: " + id));
        validarCambioEstado(reservacion, nuevoEstado);
        reservacion.setEstado(nuevoEstado);
        return reservacionRepository.save(reservacion);
    }

    // Valida todos los campos, verifica disponibilidad, detecta traslapes y calcula el precio total
    private void prepararReservacion(ReservacionModel reservacion, Long idReservacion) {
        if (reservacion == null) {
            throw new IllegalArgumentException("La reservación es obligatoria");
        }
        validarFechas(reservacion.getFechaEntrada(), reservacion.getFechaSalida());
        validarFechasDeEstancia(reservacion);
        if (reservacion.getNumeroHuespedes() == null || reservacion.getNumeroHuespedes() < 1) {
            throw new IllegalArgumentException("El número de huéspedes debe ser mayor que cero");
        }
        if (reservacion.getPropiedad() == null || reservacion.getPropiedad().getIdUnidad() == null) {
            throw new IllegalArgumentException("La unidad rentable es obligatoria");
        }
        if (reservacion.getCliente() == null || reservacion.getCliente().getIdUsuario() == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        reservacion.setCliente(usuarioRepository.findById(reservacion.getCliente().getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("El cliente no existe")));

        Long idUnidad = reservacion.getPropiedad().getIdUnidad();
        UnidadRentableModel unidad = unidadRentableRepository.findById(idUnidad)
                .orElseThrow(() -> new IllegalArgumentException("La unidad rentable no existe: " + idUnidad));
        reservacion.setPropiedad(unidad);
        if (unidad.getCapacidadPersonas() == null || reservacion.getNumeroHuespedes() > unidad.getCapacidadPersonas()) {
            throw new IllegalArgumentException("La unidad no tiene capacidad para todos los huéspedes");
        }
        // Verifica que no existan otras reservaciones activas en el mismo periodo (traslape)
        if (reservacionRepository.existeTraslape(
                idUnidad,
                List.of(ReservacionModel.Estado.pendiente, ReservacionModel.Estado.confirmada,
                        ReservacionModel.Estado.en_curso),
                reservacion.getFechaEntrada(),
                reservacion.getFechaSalida(),
                idReservacion)) {
            throw new IllegalStateException("La unidad ya está reservada para las fechas solicitadas");
        }

        // Busca un periodo de disponibilidad que cubra las fechas solicitadas
        DisponibilidadModel disponibilidad = disponibilidadRepository.findByUnidad_IdUnidad(idUnidad).stream()
            .filter(item -> Boolean.TRUE.equals(item.getDisponible())
                && !item.getFechaInicio().isAfter(reservacion.getFechaEntrada())
                        && !item.getFechaFin().isBefore(reservacion.getFechaSalida()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("La unidad no está disponible para las fechas solicitadas"));

        // Usa el precio especial del periodo si existe, de lo contrario usa el precio base de la unidad
        BigDecimal precioNoche = disponibilidad.getPrecioEspecial() != null
                ? disponibilidad.getPrecioEspecial()
                : unidad.getPrecioNoche();
        if (precioNoche == null || precioNoche.signum() < 0) {
            throw new IllegalStateException("La unidad no tiene un precio por noche válido");
        }
        long noches = ChronoUnit.DAYS.between(reservacion.getFechaEntrada(), reservacion.getFechaSalida());
        reservacion.setPrecioTotal(precioNoche.multiply(BigDecimal.valueOf(noches)));
        if (reservacion.getCodigoReserva() == null || reservacion.getCodigoReserva().isBlank()) {
            reservacion.setCodigoReserva(generarCodigoReserva()); // genera código único basado en timestamp
        }
        if (reservacion.getEstado() == null || reservacion.getIdReservacion() == null) {
            reservacion.setEstado(ReservacionModel.Estado.pendiente); // estado inicial por defecto
        }
    }

    // Valida que la fecha de entrada sea anterior a la fecha de salida
    private void validarFechas(LocalDate fechaEntrada, LocalDate fechaSalida) {
        if (fechaEntrada == null || fechaSalida == null || !fechaEntrada.isBefore(fechaSalida)) {
            throw new IllegalArgumentException("La fecha de entrada debe ser anterior a la fecha de salida");
        }
    }

    // Valida la coherencia de las fechas de check-in y checkout respecto a las fechas de estancia
    private void validarFechasDeEstancia(ReservacionModel reservacion) {
        LocalDate fechaEntrada = reservacion.getFechaEntrada();
        LocalDate fechaSalida = reservacion.getFechaSalida();
        LocalDateTime fechaCheckin = reservacion.getFechaCheckin();
        LocalDateTime fechaCheckout = reservacion.getFechaCheckout();
        LocalDateTime ahora = LocalDateTime.now();

        if (fechaCheckin != null && fechaCheckin.toLocalDate().isBefore(fechaEntrada)) {
            throw new IllegalArgumentException("El check-in no puede ser anterior a la fecha de entrada");
        }
        if (fechaCheckout != null && fechaCheckout.toLocalDate().isBefore(fechaSalida)) {
            throw new IllegalArgumentException("El checkout no puede ser anterior a la fecha de salida");
        }
        if (fechaCheckin != null && fechaCheckin.isAfter(ahora)) {
            throw new IllegalArgumentException("El check-in no puede estar en el futuro");
        }
        if (fechaCheckout != null && fechaCheckout.isAfter(ahora)) {
            throw new IllegalArgumentException("El checkout no puede estar en el futuro");
        }
        if (fechaCheckin != null && fechaCheckout != null && !fechaCheckin.isBefore(fechaCheckout)) {
            throw new IllegalArgumentException("El checkout debe ser posterior al check-in");
        }
    }

    // Valida que la transición de estado sea permitida; verifica condiciones adicionales para en_curso y completada
    // Máquina de estados: pendiente → confirmada | cancelada
    //                     confirmada → en_curso | cancelada | no_presento
    //                     en_curso → completada
    //                     completada | cancelada | no_presento → (estado final)
    private void validarCambioEstado(ReservacionModel reservacion,
            ReservacionModel.Estado nuevoEstado) {
        ReservacionModel.Estado estadoActual = reservacion.getEstado();
        if (estadoActual == nuevoEstado) {
            return;
        }
        boolean valido = switch (estadoActual) {
            case pendiente -> nuevoEstado == ReservacionModel.Estado.confirmada
                    || nuevoEstado == ReservacionModel.Estado.cancelada;
            case confirmada -> nuevoEstado == ReservacionModel.Estado.en_curso
                    || nuevoEstado == ReservacionModel.Estado.cancelada
                    || nuevoEstado == ReservacionModel.Estado.no_presento;
            case en_curso -> nuevoEstado == ReservacionModel.Estado.completada;
            case completada, cancelada, no_presento -> false;
        };
        if (!valido) {
            throw new IllegalStateException("Cambio de estado no permitido: "
                    + estadoActual + " -> " + nuevoEstado);
        }
        // Para pasar a en_curso se requiere check-in registrado y que la fecha actual esté dentro del periodo
        if (nuevoEstado == ReservacionModel.Estado.en_curso) {
            if (reservacion.getFechaCheckin() == null) {
                throw new IllegalStateException("La reservación requiere check-in para estar en curso");
            }
            if (LocalDate.now().isBefore(reservacion.getFechaEntrada())
                    || !LocalDate.now().isBefore(reservacion.getFechaSalida())) {
                throw new IllegalStateException("La fecha actual está fuera del periodo de la reservación");
            }
        }
        // Para completar se requiere checkout registrado y que la fecha de salida ya haya pasado
        if (nuevoEstado == ReservacionModel.Estado.completada) {
            if (reservacion.getFechaCheckout() == null) {
                throw new IllegalStateException("La reservación requiere checkout para completarse");
            }
            if (LocalDate.now().isBefore(reservacion.getFechaSalida())) {
                throw new IllegalStateException("La reservación no puede completarse antes de la fecha de salida");
            }
        }
    }

    // Valida que el pago inicial sea mayor que cero y no supere el total de la reservación
    private void validarMontoInicial(PagoModel pagoInicial, BigDecimal precioTotal) {
        if (pagoInicial.getMonto() == null || pagoInicial.getMonto().signum() <= 0
                || pagoInicial.getMonto().compareTo(precioTotal) > 0) {
            throw new IllegalArgumentException("El monto del pago inicial debe ser mayor que cero y no superar el total");
        }
        if (pagoInicial.getEstado() == null) {
            pagoInicial.setEstado(PagoModel.Estado.pendiente);
        }
    }

    // Genera un código de reservación único basado en el timestamp actual
    private String generarCodigoReserva() {
        return "COD-" + System.currentTimeMillis();
    }

    // Elimina una reservación por ID; retorna false si falla (por ejemplo, si tiene pagos asociados)
    public boolean eliminarReservacion(Long id) {
        try { reservacionRepository.deleteById(id); return true; } catch (Exception e) { return false; }
    }

    // Filtra reservaciones por cliente
    public ArrayList<ReservacionModel> obtenerPorCliente(Long idUsuario) { return reservacionRepository.findByCliente_IdUsuario(idUsuario); }
    // Filtra reservaciones por unidad rentable
    public ArrayList<ReservacionModel> obtenerPorPropiedad(Long idUnidad) { return reservacionRepository.findByPropiedad_IdUnidad(idUnidad); }
    // Filtra por fecha de entrada exacta
    public ArrayList<ReservacionModel> obtenerPorFechaEntrada(LocalDate fecha) { return reservacionRepository.findByFechaEntrada(fecha); }
    // Filtra por estado de la reservación
    public ArrayList<ReservacionModel> obtenerPorEstado(ReservacionModel.Estado estado) { return reservacionRepository.findByEstado(estado); }
    // Busca por código de reservación único
    public ArrayList<ReservacionModel> obtenerPorCodigo(String codigo) { return reservacionRepository.findByCodigoReserva(codigo); }
}