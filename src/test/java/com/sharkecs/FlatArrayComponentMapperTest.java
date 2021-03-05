package com.sharkecs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FlatArrayComponentMapperTest {

    @Test
    void test() {
        ComponentMapper<Object> mapper = new FlatArrayComponentMapper<>(5, Object::new);
        mapper.create(2);
        mapper.create(4);
        mapper.create(10);

        Assertions.assertNotNull(mapper.get(2));
        Assertions.assertNotNull(mapper.get(4));
        Assertions.assertNotNull(mapper.get(10));
        Assertions.assertNotSame(mapper.get(4), mapper.get(10));

        mapper.remove(2);
        Assertions.assertNull(mapper.get(2));
        Assertions.assertNotNull(mapper.get(4));
    }
}
