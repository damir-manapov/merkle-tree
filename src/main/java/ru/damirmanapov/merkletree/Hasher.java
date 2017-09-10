package ru.damirmanapov.merkletree;

public interface Hasher<T> {

    String hash(T value);
}
