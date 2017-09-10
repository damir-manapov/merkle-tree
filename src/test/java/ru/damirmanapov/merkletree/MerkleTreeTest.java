package ru.damirmanapov.merkletree;

import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Test
public class MerkleTreeTest {

    @Test
    public void hash() throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest("test string".getBytes(StandardCharsets.UTF_8));

        StringBuilder hashRepresentation = new StringBuilder();

        for (byte b : hash) {
            hashRepresentation.append(String.format("%02x", b));
        }

        assertThat(hashRepresentation.toString(), is("d5579c46dfcc7f18207013e65b44e4cb4e2c2298f4ac457ba8f82743f31e930b"));
    }

    @Test
    public void oneValue() throws NoSuchAlgorithmException {

        MerkleTree<String> tree = new MerkleTree();
        tree.setValueHasher(MerkleTree::hash);
        tree.add("test string 1");

        assertThat(tree.getRootHash(), is("393157d4e7e98f622bdac3b45aa324a9770fc4e017c7a31e47ef191374161f1c"));
        assertThat(((LeafNode) tree.getRoot()).getValue(), is("test string 1"));
        assertThat(tree.getRoot().getChildren().size(), is(0));

        assertThat(depth(tree.getRoot()), is(1));
    }

    @Test
    public void twoValue() throws NoSuchAlgorithmException {

        MerkleTree<String> tree = new MerkleTree();
        tree.setValueHasher(MerkleTree::hash);
        tree.add("test string 1");
        tree.add("test string 2");

        assertThat(tree.getRootHash(), is("b710a339c625773c138198bfce2830da6492c43cf78d3949ed22ca9841299c5bc26a1165821660f5c5c2347a39155b7e33b2f3592c85e30975c7d185404c7dff"));
        assertThat(tree.getRoot().getChildren().size(), is(2));

        assertThat(((LeafNode) tree.getRoot().getChildren().get(0)).getValue(), is("test string 1"));
        assertThat(tree.getRoot().getChildren().get(0).getChildren().size(), is(0));

        assertThat(((LeafNode) tree.getRoot().getChildren().get(1)).getValue(), is("test string 2"));
        assertThat(tree.getRoot().getChildren().get(1).getChildren().size(), is(0));

        assertThat(depth(tree.getRoot()), is(2));
    }

    @Test
    public void threeValue() throws NoSuchAlgorithmException {

        MerkleTree<String> tree = new MerkleTree();
        tree.setValueHasher(value -> MerkleTree.hash(value));
        tree.add("test string 1");
        tree.add("test string 2");
        tree.add("test string 3");

        assertThat(tree.getRootHash(), is("709bd0771d8127dc2352fac55b3ff34469709004d8ee7041a7dab38c40c0bdf7672c99a642e47d40940d67e38db4c672c9ef97d5b3dd14f2f9c4f8308a96d18d"));
        assertThat(tree.getRoot().getChildren().size(), is(2));

        assertThat(tree.getRoot().getChildren().get(0).getChildren().size(), is(2));

        assertThat(((LeafNode) tree.getRoot().getChildren().get(0).getChildren().get(0)).getValue(), is("test string 1"));
        assertThat(tree.getRoot().getChildren().get(0).getChildren().get(0).getChildren().size(), is(0));

        assertThat(((LeafNode) tree.getRoot().getChildren().get(0).getChildren().get(1)).getValue(), is("test string 2"));
        assertThat(tree.getRoot().getChildren().get(0).getChildren().get(1).getChildren().size(), is(0));

        assertThat(tree.getRoot().getChildren().get(1).getChildren().size(), is(1));
        assertThat(((LeafNode) tree.getRoot().getChildren().get(1).getChildren().get(0)).getValue(), is("test string 3"));
        assertThat(tree.getRoot().getChildren().get(1).getChildren().get(0).getChildren().size(), is(0));

        assertThat(depth(tree.getRoot()), is(3));
    }

    @Test
    public void depth() throws NoSuchAlgorithmException {

        Hasher hasher = (Hasher<String>) MerkleTree::hash;

        MerkleTree<String> zeroTree = new MerkleTree();
        zeroTree.setValueHasher(hasher);
        assertThat(depth(zeroTree.getRoot()), is(0));

        MerkleTree<String> oneTree = new MerkleTree();
        oneTree.setValueHasher(hasher);
        oneTree.add("test string 1");
        assertThat(depth(oneTree.getRoot()), is(1));

        MerkleTree<String> twoTree = new MerkleTree();
        twoTree.setValueHasher(hasher);
        twoTree.add("test string 1");
        twoTree.add("test string 2");
        assertThat(depth(twoTree.getRoot()), is(2));

        MerkleTree<String> threeTree = new MerkleTree();
        threeTree.setValueHasher(hasher);
        threeTree.add("test string 1");
        threeTree.add("test string 2");
        threeTree.add("test string 3");
        assertThat(depth(threeTree.getRoot()), is(3));

        MerkleTree<String> fourTree = new MerkleTree();
        fourTree.setValueHasher(hasher);
        fourTree.add("test string 1");
        fourTree.add("test string 2");
        fourTree.add("test string 3");
        fourTree.add("test string 4");
        assertThat(depth(fourTree.getRoot()), is(3));

        MerkleTree<String> fiveTree = new MerkleTree();
        fiveTree.setValueHasher(hasher);
        fiveTree.add("test string 1");
        fiveTree.add("test string 2");
        fiveTree.add("test string 3");
        fiveTree.add("test string 4");
        fiveTree.add("test string 5");
        assertThat(depth(fiveTree.getRoot()), is(4));
    }

    private int depth(Node node) {
        if (node == null) {
            return 0;
        }
        return depth(1, node);
    }

    private int depth(int depth, Node node) {

        int currenDepth = 0;

        List<Node> children = node.getChildren();

        if (children.size() == 0) {
            return depth;
        }

        for (Node child : children) {
            currenDepth = Math.max(depth, depth(depth + 1, child));
        }

        return Math.max(currenDepth, depth);
    }

}