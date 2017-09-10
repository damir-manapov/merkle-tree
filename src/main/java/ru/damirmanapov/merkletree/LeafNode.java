package ru.damirmanapov.merkletree;

public class LeafNode<U extends Comparable<U>> extends Node {

    private U value;
    private MerkleTree<U> tree;

    public LeafNode(U value, MerkleTree<U> tree) {
        this.value = value;
        this.tree = tree;
    }

    public String hash() {
        return MerkleTree.hash(String.valueOf(tree.getValueHasher().hash(this.getValue())));
    }

    public U getValue() {
        return value;
    }

    public void setValue(U value) {
        this.value = value;
    }

}

