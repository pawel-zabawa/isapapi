package pl.twojaFirma.ipapviewer.service;

import com.github.benmanes.caffeine.cache.Cache;
import pl.twojaFirma.ipapviewer.infra.CacheConfig;
import pl.twojaFirma.ipapviewer.model.Act;
import pl.twojaFirma.ipapviewer.model.ActPage;
import pl.twojaFirma.ipapviewer.model.Category;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ActService {

    private final IpapClient ipapClient;
    private final Cache<String, Object> categoryCache = CacheConfig.createCategoryCache();
    private final Cache<String, Object> actCache = CacheConfig.createActCache();
    private String currentCategory;
    private String nextCursor;
    private final Deque<Act> buffer = new ArrayDeque<>();
    private final List<Act> history = new ArrayList<>();

    public ActService(IpapClient ipapClient) {
        this.ipapClient = ipapClient;
    }

    @SuppressWarnings("unchecked")
    public List<Category> categories() {
        return (List<Category>) categoryCache.get("categories", key -> ipapClient.listCategories());
    }

    public Optional<Act> current() {
        if (history.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(history.get(history.size() - 1));
    }

    public Optional<Act> previous() {
        if (history.size() <= 1) {
            return Optional.empty();
        }
        history.remove(history.size() - 1);
        return current();
    }

    public Act next(String categoryId) {
        if (!Objects.equals(categoryId, currentCategory)) {
            resetCategory(categoryId);
        }
        if (buffer.isEmpty()) {
            fetchNextPage();
        }
        Act next = buffer.removeFirst();
        history.add(next);
        actCache.put(next.id(), next);
        return next;
    }

    private void resetCategory(String categoryId) {
        buffer.clear();
        history.clear();
        currentCategory = categoryId;
        nextCursor = null;
    }

    private void fetchNextPage() {
        ActPage page = ipapClient.listActs(currentCategory, nextCursor);
        buffer.addAll(page.items());
        nextCursor = page.nextCursor();
    }
}
