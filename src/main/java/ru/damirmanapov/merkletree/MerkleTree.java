package ru.damirmanapov.merkletree;

import ru.damirmanapov.exception.AppException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class MerkleTree<T extends Comparable<T>> {

    private Node root;
    private Hasher<T> valueHasher;
    private List<LeafNode<T>> leaves = new LinkedList();

    public static String hash(String value) {

        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));

            StringBuilder hashRepresentation = new StringBuilder();

            for (byte b : hash) {
                hashRepresentation.append(String.format("%02x", b));
            }

            return hashRepresentation.toString();

        } catch (NoSuchAlgorithmException e) {

            throw new AppException(e);

        }
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public Hasher<T> getValueHasher() {
        return valueHasher;
    }

    public void setValueHasher(Hasher<T> valueHasher) {
        this.valueHasher = valueHasher;
    }

    public List<LeafNode<T>> getLeaves() {
        return leaves;
    }

    public void setLeaves(List<LeafNode<T>> leaves) {
        this.leaves = leaves;
    }

    private void rebuildTree() {

        leaves.sort(Comparator.comparing(LeafNode::getValue));

        List<Node> currentLevelNodes = new LinkedList(leaves);
        List<Node> nextLevelNodes = new LinkedList();

        while (currentLevelNodes.size() > 1) {

            for (int i = 0; i < currentLevelNodes.size(); i += 2) {

                Node nextLevelNode = new Node();
                List<Node> children = new LinkedList<>();

                if (currentLevelNodes.size() - i >= 2) {
                    children.add(currentLevelNodes.get(i));
                    children.add(currentLevelNodes.get(i + 1));
                } else {
                    children.add(currentLevelNodes.get(i));
                }

                nextLevelNode.setChildren(children);
                nextLevelNodes.add(nextLevelNode);

            }

            currentLevelNodes = new LinkedList(nextLevelNodes);
            nextLevelNodes = new LinkedList();

        }

        setRoot(currentLevelNodes.get(0));

    }

    public String getRootHash() {
        return getRoot().hash();
    }

    public void add(T value) {
        leaves.add(new LeafNode<>(value, this));
        rebuildTree();
    }

}
