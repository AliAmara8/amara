//package com.ali.amara.diagnostic.controller;
//
//import com.ali.amara.diagnostic.dto.DiagnosticRequest;
//import com.ali.amara.diagnostic.dto.DiagnosticResultDTO;
//import com.ali.amara.diagnostic.service.DiagnosticService;
//import com.ali.amara.user.entity.User;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/diagnostics")
//@RequiredArgsConstructor
//public class DiagnosticController {
//
//    private final DiagnosticService diagnosticService;
//
//    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<DiagnosticResultDTO> uploadDiagnosticImage(
//            @RequestPart("image") MultipartFile imageFile,
//            @AuthenticationPrincipal User currentUser) {
//
//        // La logique d'upload, d'appel de l'IA, et de génération de la recommandation
//        DiagnosticResultDTO result = diagnosticService.processImage(imageFile, currentUser);
//        return ResponseEntity.ok(result);
//    }
//
//    // Un endpoint pour un diagnostic basé sur du texte (si l'utilisateur décrit les symptômes)
//    @PostMapping("/text-symptoms")
//    public ResponseEntity<DiagnosticResultDTO> diagnoseFromText(@RequestBody DiagnosticRequest request,
//                                                                @AuthenticationPrincipal User currentUser) {
//        DiagnosticResultDTO result = diagnosticService.processText(request, currentUser);
//        return ResponseEntity.ok(result);
//    }
//}