package nurses.planning.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report {
    private Map<String, Integer> invalidRules;

    public Report() {
        this.invalidRules = new HashMap<>();
    }

    public void addFailedRule(String ruleName, int agentNb) {
        this.invalidRules.put(ruleName, agentNb);
    }
}
