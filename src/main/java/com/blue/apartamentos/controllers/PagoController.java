package com.blue.apartamentos.controllers;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.blue.apartamentos.models.PagoModel;
import com.blue.apartamentos.services.PagoService;

// API REST para la gestión de pagos vinculados a reservaciones
@RestController
@RequestMapping("/api/pagos")
public class PagoController {
    private final PagoService pagoService;

    public PagoController(PagoService pagoService) { this.pagoService = pagoService; }

    // GET /api/pagos — lista todos los pagos del sistema
    @GetMapping
    public List<PagoModel> obtenerPagos() { return pagoService.obtenerPagos(); }

    // GET /api/pagos/{id} — obtiene un pago por ID; 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<PagoModel> obtenerPorId(@PathVariable Long id) {
        return pagoService.obtenerPorId(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/pagos — registra un nuevo pago; devuelve 201 Created
    @PostMapping
    public ResponseEntity<PagoModel> guardar(@RequestBody PagoModel pago) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(pagoService.guardarPago(pago));
    }

    // PUT /api/pagos/{id} — actualiza datos de un pago existente
    @PutMapping("/{id}")
    public ResponseEntity<PagoModel> actualizar(@RequestBody PagoModel pago, @PathVariable Long id) {
        return ResponseEntity.ok(pagoService.actualizarPago(pago, id));
    }

    // DELETE /api/pagos/{id} — elimina un pago; 204 si tuvo éxito, 404 si no existe
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return pagoService.eliminarPago(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // GET /api/pagos/reservacion/{idReservacion} — lista todos los pagos de una reservación
    @GetMapping("/reservacion/{idReservacion}")
    public List<PagoModel> obtenerPorReservacion(@PathVariable Long idReservacion) {
        return pagoService.obtenerPorReservacion(idReservacion);
    }

    // GET /api/pagos/cliente/{idUsuario} — lista pagos del cliente indicado
    @GetMapping("/cliente/{idUsuario}")
    public List<PagoModel> obtenerPorCliente(@PathVariable Long idUsuario) {
        return pagoService.obtenerPorCliente(idUsuario);
    }

    // GET /api/pagos/fecha-pago?fecha=... — filtra pagos por fecha/hora exacta
    @GetMapping("/fecha-pago")
    public List<PagoModel> obtenerPorFechaPago(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fecha) {
        return pagoService.obtenerPorFechaPago(fecha);
    }

    // GET /api/pagos/estado/{estado} — filtra pagos por estado (pendiente, aceptado, etc.)
    @GetMapping("/estado/{estado}")
    public List<PagoModel> obtenerPorEstado(@PathVariable PagoModel.Estado estado) {
        return pagoService.obtenerPorEstado(estado);
    }

    // GET /api/pagos/referencia/{referencia} — busca pagos por referencia de transacción
    @GetMapping("/referencia/{referencia}")
    public List<PagoModel> obtenerPorReferencia(@PathVariable String referencia) {
        return pagoService.obtenerPorReferencia(referencia);
    }

    // GET /api/pagos/reservacion/{idReservacion}/pagada — indica si la reservación está saldada
    @GetMapping("/reservacion/{idReservacion}/pagada")
    public boolean estaPagada(@PathVariable Long idReservacion) {
        return pagoService.estaPagada(idReservacion);
    }
}