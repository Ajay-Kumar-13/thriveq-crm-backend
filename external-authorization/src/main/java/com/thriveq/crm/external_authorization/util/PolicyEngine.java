package com.thriveq.crm.external_authorization.util;

import com.thriveq.crm.external_authorization.config.RbacPolicy;
import jakarta.annotation.PostConstruct;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PolicyEngine {

    private final List<CompiledRule> rules;

    private final PathPatternParser parser = new PathPatternParser();

    record CompiledRule(String method, PathPattern pattern, String permission) {}

    public PolicyEngine(RbacPolicy rbacPolicy) {
        this.rules = rbacPolicy.rules()
                .stream()
                .map(rule -> new CompiledRule(rule.method(), parser.parse(rule.path()), rule.permission()))
                .toList();
    }

    public Optional<String> requiredPermissions(String method, String path){
        PathContainer p = PathContainer.parsePath(path);
        return rules
                .stream()
                .filter(compiledRule -> compiledRule.method().equals("*") || compiledRule.method().equalsIgnoreCase(method))
                .filter(compiledRule -> compiledRule.pattern().matches(p))
                .findFirst()
                .map(CompiledRule::permission);
    }
}
