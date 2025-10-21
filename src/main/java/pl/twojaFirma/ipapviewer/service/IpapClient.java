package pl.twojaFirma.ipapviewer.service;

import pl.twojaFirma.ipapviewer.model.Act;
import pl.twojaFirma.ipapviewer.model.ActPage;
import pl.twojaFirma.ipapviewer.model.Category;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Minimal stub IPAP client that exposes the expected contract and returns deterministic sample data.
 * In a real deployment this class should call the remote HTTPS API.
 */
public class IpapClient {

    public List<Category> listCategories() {
        return List.of(
                new Category("health", "Zdrowie"),
                new Category("education", "Edukacja"),
                new Category("economy", "Gospodarka")
        );
    }

    public ActPage listActs(String categoryId, String cursor) {
        List<Act> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String id = UUID.nameUUIDFromBytes((categoryId + cursor + i).getBytes()).toString();
            items.add(new Act(
                    id,
                    "Akt " + categoryId + " #" + cursor + i,
                    LocalDate.now().minusDays(ThreadLocalRandom.current().nextInt(1000)),
                    categoryId,
                    "Treść aktu " + id + "..."));
        }
        String nextCursor = cursor == null ? "1" : String.valueOf(Integer.parseInt(cursor) + 1);
        return new ActPage(items, nextCursor);
    }

    public Act getAct(String actId) {
        return new Act(
                actId,
                "Akt " + actId.substring(0, 8),
                LocalDate.now().minusDays(10),
                "unknown",
                "Szczegółowa treść aktu " + actId + "."
        );
    }
}
