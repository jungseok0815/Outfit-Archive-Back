package com.fasthub.backend.admin.keyword.controller;

import com.fasthub.backend.admin.keyword.dto.InsertKeywordDto;
import com.fasthub.backend.admin.keyword.dto.ResponseKeywordDto;
import com.fasthub.backend.admin.keyword.service.CollectKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/keyword")
@RequiredArgsConstructor
public class CollectKeywordController {

    private final CollectKeywordService collectKeywordService;

    @GetMapping("/list")
    public ResponseEntity<List<ResponseKeywordDto>> list() {
        return ResponseEntity.ok(collectKeywordService.list());
    }

    @PostMapping("/insert")
    public ResponseEntity<Void> insert(@RequestBody InsertKeywordDto dto) {
        collectKeywordService.insert(dto);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam Long id) {
        collectKeywordService.delete(id);
        return ResponseEntity.ok().build();
    }
}
