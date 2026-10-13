package com.blue.apartamentos.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.blue.apartamentos.models.ReembolsoModel;
import com.blue.apartamentos.services.ReembolsoService;

// API REST para la gestión de reembolsos de pagos
// Base path: /api — comparte prefijo con reservaciones para la ruta de reembolso por reservación
@RestController
@RequestMapping("/api")
public class ReembolsoController {
    private final ReembolsoService reembolsoService;

    public ReembolsoController(ReembolsoService reembolsoService) { this.reembolsoService = reembolsoService; }

    // GET /api/reembolsos — lista todos los reembolsos
    @GetMapping("/reembolsos")
    public List<ReembolsoModel> obtenerReembolsos() { return reembolsoService.obtenerReembolsos(); }

    // GET /api/reembolsos/{id} — obtiene un reembolso por ID; 404 si no existe
    @GetMapping("/reembolsos/{id}")
    public ResponseEntity<ReembolsoModel> obtenerPorId(@PathVariable Long id) {
        return reembolsoService.obtenerPorId(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/reembolsos — registra un reembolso directo sin pasar por reservación; devuelve 201 Created
    @PostMapping("/reembolsos")
    public ResponseEntity<ReembolsoModel> guardarDirecto(@RequestBody ReembolsoModel reembolso) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(reembolsoService.guardarReembolso(reembolso));
    }

    // POST /api/reservaciones/{id}/reembolso — registra reembolso vinculado a una reservación cancelada
    @PostMapping("/reservaciones/{id}/reembolso")
    public ResponseEntity<ReembolsoModel> guardar(@PathVariable Long id, @RequestBody ReembolsoModel reembolso) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(reembolsoService.guardarReembolsoParaReservacion(id, reembolso));
    }

    // PUT /api/reembolsos/{id} — actualiza el monto de un reembolso
    @PutMapping("/reembolsos/{id}")
    public ResponseEntity<ReembolsoModel> actualizar(@RequestBody ReembolsoModel reembolso, @PathVariable Long id) {
        return ResponseEntity.ok(reembolsoService.actualizarReembolso(reembolso, id));
    }

    // PATCH /api/reembolsos/{id}/estado — avanza el estado del reembolso (pendiente → aprobado → procesado)
    @PatchMapping("/reembolsos/{id}/estado")
    public ResponseEntity<ReembolsoModel> cambiarEstado(@PathVariable Long id, @RequestBody ReembolsoModel request) {
        return ResponseEntity.ok(reembolsoService.cambiarEstadoReembolso(id, request.getEstado()));
    }

    // DELETE /api/reembolsos/{id} — elimina un reembolso; 204 si tuvo éxito, 404 si no existe
    @DeleteMapping("/reembolsos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return reembolsoService.eliminarReembolso(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // GET /api/pagos/{idPago}/monto-reembolsable — calcula cuánto queda disponible para reembolsar de un pago
    @GetMapping("/pagos/{idPago}/monto-reembolsable")
    public BigDecimal obtenerMontoReembolsable(@PathVariable Long idPago) {
        return reembolsoService.obtenerMontoReembolsable(idPago);
    }

    // GET /api/reembolsos/pago/{idPago} — lista reembolsos de un pago específico
    @GetMapping("/reembolsos/pago/{idPago}")
    public List<ReembolsoModel> obtenerPorPago(@PathVariable Long idPago) { return reembolsoService.obtenerPorPago(idPago); }

    // GET /api/reembolsos/cliente/{idUsuario} — lista reembolsos de un cliente
    @GetMapping("/reembolsos/cliente/{idUsuario}")
    public List<ReembolsoModel> obtenerPorCliente(@PathVariable Long idUsuario) { return reembolsoService.obtenerPorCliente(idUsuario); }

    // GET /api/reembolsos/fecha?fecha=... — filtra reembolsos por fecha/hora exacta
    @GetMapping("/reembolsos/fecha")
    public List<ReembolsoModel> obtenerPorFecha(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fecha) { return reembolsoService.obtenerPorFecha(fecha); }

    // GET /api/reembolsos/monto?monto=... — filtra reembolsos por monto exacto
    @GetMapping("/reembolsos/monto")
    public List<ReembolsoModel> obtenerPorMonto(@RequestParam BigDecimal monto) { return reembolsoService.obtenerPorMonto(monto); }
}