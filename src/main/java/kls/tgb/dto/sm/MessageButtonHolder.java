package kls.tgb.dto.sm;

import java.util.Map;

public record MessageButtonHolder (
        String message,
        Map<String, UserAction> buttons
) {
}
