package kls.tgb.dto.sm;

import java.util.List;
import java.util.Map;

public record MessageButtonHolder (
        String message,
        Map<String, String> buttons
) {
}
