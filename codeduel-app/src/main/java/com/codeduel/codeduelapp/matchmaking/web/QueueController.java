package com.codeduel.codeduelapp.matchmaking.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matchmaking")
public class QueueController {

    @PostMapping("/queue")
    public void findMatch() {

    }

    @DeleteMapping("/queue")
    public void stopFindMatch() {

    }

    @GetMapping("/status")
    public void getFindMatchStatus() {

    }
}