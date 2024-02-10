package com.iridium.iridiumfactions.managers;

public interface DatabaseKey<Key, Value> {
    Key getKey(Value input);
}