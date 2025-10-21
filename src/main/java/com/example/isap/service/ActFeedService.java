package com.example.isap.service;

import com.example.isap.model.Act;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ActFeedService {
    private final IsapApiClient apiClient;
    private final ActContentService contentService;

    private final List<Act> cachedActs = new CopyOnWriteArrayList<>();

    public ActFeedService(IsapApiClient apiClient, ActContentService contentService) {
        this.apiClient = apiClient;
        this.contentService = contentService;
    }

    public void refresh(String publisher, int year, int limit) throws IOException, InterruptedException {
        List<Act> acts = apiClient.fetchActs(publisher, year, limit);
        List<Act> filtered = acts.stream()
                .filter(act -> !act.isInForce())
                .filter(act -> act.getTitle() != null && act.getTitle().toLowerCase().contains("projekt"))
                .sorted(Comparator.comparing(Act::getPromulgationDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
        cachedActs.clear();
        cachedActs.addAll(filtered);
    }

    public List<Act> listActs() {
        return List.copyOf(cachedActs);
    }

    public List<String> availableTopics() {
        return apiClient.collectKeywords(cachedActs);
    }

    public Optional<Act> nextAct(int index, String selectedKeyword) {
        if (cachedActs.isEmpty() || index < 0) {
            return Optional.empty();
        }
        List<Act> acts = filterByKeyword(selectedKeyword);
        if (acts.isEmpty() || index >= acts.size()) {
            return Optional.empty();
        }
        return Optional.of(acts.get(index));
    }

    public List<Act> filterByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.copyOf(cachedActs);
        }
        return cachedActs.stream()
                .filter(act -> act.getKeywords().stream().anyMatch(k -> k.equalsIgnoreCase(keyword)))
                .collect(Collectors.toList());
    }

    public String loadActText(Act act) {
        String text = contentService.fetchPlainText(act);
        if (text == null || text.isBlank()) {
            return act.getTitle();
        }
        return text;
    }
}
