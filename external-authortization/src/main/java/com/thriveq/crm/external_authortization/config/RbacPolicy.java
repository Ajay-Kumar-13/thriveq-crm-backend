package com.thriveq.crm.external_authortization.config;

import java.util.List;

/**
 * @param rules
 * rules are from the rbac-policy.yml file, being injected from the RbacPolicyLoader file.
 * A record is a short way to write a class that just holds data, The record version generates all of that for you,
 * constructor, getters, equals, hashCode, toString. You write one line instead of twenty
 */
public record RbacPolicy(List<Rule> rules) {
    public record Rule(String method, String path, String permissions) {}
}
