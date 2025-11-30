package com.example.mdai.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FaviconController {

    // Responde /favicon.ico con un pequeño SVG como favicon para evitar NoResourceFoundException
    @GetMapping(value = "/favicon.ico", produces = "image/svg+xml")
    public ResponseEntity<String> favicon() {
        String svg = "<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 16 16'>"
                + "<rect width='100%' height='100%' fill='%230ea5a4'/>"
                + "<text x='50%' y='55%' font-size='9' text-anchor='middle' fill='white' font-family='Arial, Helvetica, sans-serif'>T1</text>"
                + "</svg>";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("image/svg+xml"));
        return new ResponseEntity<>(svg, headers, HttpStatus.OK);
    }
}

