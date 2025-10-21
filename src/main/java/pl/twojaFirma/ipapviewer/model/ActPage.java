package pl.twojaFirma.ipapviewer.model;

import java.util.List;

public record ActPage(List<Act> items, String nextCursor) {
}
