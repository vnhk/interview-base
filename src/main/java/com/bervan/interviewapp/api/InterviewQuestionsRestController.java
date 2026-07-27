package com.bervan.interviewapp.api;

import com.bervan.common.config.EntityConfigValidator;
import com.bervan.common.controller.BaseOwnedController;
import com.bervan.common.controller.ImportResult;
import com.bervan.common.mapper.BervanDTOMapper;
import com.bervan.common.search.model.SortDirection;
import com.bervan.interviewapp.interviewquestions.InterviewQuestionService;
import com.bervan.interviewapp.interviewquestions.Question;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/interview/questions")
public class InterviewQuestionsRestController extends BaseOwnedController {

    private final InterviewQuestionService questionService;

    protected InterviewQuestionsRestController(InterviewQuestionService service, BervanDTOMapper mapper, EntityConfigValidator validator) {
        super(service, mapper, validator, "Question");
        this.questionService = service;
    }

    @GetMapping("/tags")
    public ResponseEntity<List<String>> getTags() {
        Set<String> tags = questionService.getAllDistinctTags();
        return ResponseEntity.ok(tags.stream().sorted().collect(Collectors.toList()));
    }

    @GetMapping("/by-tag-difficulty")
    public ResponseEntity<List<InterviewQuestionDto>> getByTagAndDifficulty(
            @RequestParam String tag,
            @RequestParam Integer difficulty) {
        List<Question> questions = questionService.findByTagsAndDifficulty(List.of(tag), difficulty);
        List<InterviewQuestionDto> dtos = questions.stream()
                .map(q -> mapper.map(q, InterviewQuestionDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterviewQuestionDto> getById(@PathVariable UUID id) {
        return super.getById(id, InterviewQuestionDto.class);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody InterviewQuestionDto req) {
        return super.create(req);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody InterviewQuestionDto req) {
        req.setId(id);
        return super.update(req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return super.delete(id);
    }

    @GetMapping
    public ResponseEntity<Page<InterviewQuestionDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) throws Exception {
        return super.load(page, size, InterviewQuestionDto.class, "id", SortDirection.ASC);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam MultiValueMap<String, String> allParams) {
        return super.exportAll(allParams, InterviewQuestionDto.class, "questions", Question.class);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportResult> importData(@RequestParam("file") MultipartFile file) {
        return super.importAll(file, InterviewQuestionDto.class);
    }
}
