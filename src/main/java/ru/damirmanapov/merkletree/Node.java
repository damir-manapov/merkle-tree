package ru.damirmanapov.merkletree;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Node {

    public List<Node> children = new LinkedList<>();

    public String hash() {
        return children
                .stream()
                .map(x -> MerkleTree.hash(String.valueOf(x.hash())))
                .collect(Collectors.joining());
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }
}
