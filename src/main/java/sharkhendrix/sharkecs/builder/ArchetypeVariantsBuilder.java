/*
 * Copyright 2024 Joannick Gardize
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package sharkhendrix.sharkecs.builder;

import sharkhendrix.sharkecs.Archetype;

import java.util.ArrayList;
import java.util.List;

/**
 * Helps to create an archetype and all its possible variations (volatile style
 * components).
 */
public class ArchetypeVariantsBuilder {

    private static class ArchetypeVariant {

        String suffix;
        Class<?> component;

        public ArchetypeVariant(String suffix, Class<?> component) {
            this.suffix = suffix;
            this.component = component;
        }
    }

    private Archetype base;
    private List<ArchetypeVariant> variants = new ArrayList<>();

    /**
     * Creates an ArchetypeVariantsBuilder with the base archetype described by the
     * parameters
     *
     * @param baseName       the name of the base archetype
     * @param baseComponents the component composition of the base archetype
     */
    public ArchetypeVariantsBuilder(String baseName, Class<?>... baseComponents) {
        base = new Archetype(baseName, baseComponents);
    }

    /**
     * Adds a variant component.
     *
     * @param suffix    the suffix added to the end of the resulting archetype name.
     *                  For multiple variants, the variant declaration order
     *                  reflects the suffix appending order
     * @param component the component type of the variant
     * @return this for chaining
     */
    public ArchetypeVariantsBuilder variant(String suffix, Class<?> component) {
        variants.add(new ArchetypeVariant(suffix, component));
        return this;
    }

    /**
     * Adds the base archetype, all its possible variants, and all possible
     * transmutations to the engine builder.
     * <p>
     * Which means it will create A = 2^N archetypes, where N is the number of
     * variants, and A^2 - A transmutations.
     *
     * @param engineBuilder the engine builder to configure with this archetype and
     *                      all its variants.
     */
    public void apply(EngineBuilder engineBuilder) {
        List<Archetype> allArchetypes = new ArrayList<>();
        allArchetypes.add(base);
        engineBuilder.with(base.getName(), base);
        long combinationCount = (long) Math.pow(2, variants.size());
        for (long mask = 1; mask < combinationCount; mask++) {
            Archetype variant = buildVariant(mask);
            allArchetypes.add(variant);
            engineBuilder.with(variant.getName(), variant);
        }
        for (int i = 0; i < allArchetypes.size(); i++) {
            for (int j = i + 1; j < allArchetypes.size(); j++) {
                engineBuilder.transmutation(allArchetypes.get(i), allArchetypes.get(j));
                engineBuilder.transmutation(allArchetypes.get(j), allArchetypes.get(i));
            }
        }
    }

    private Archetype buildVariant(long mask) {
        List<Class<?>> tmpComposition = new ArrayList<>();
        tmpComposition.addAll(base.getComposition());
        StringBuilder nameBuilder = new StringBuilder(base.getName());
        for (int i = 0; i < variants.size(); i++) {
            if ((mask & 1L << i) != 0) {
                ArchetypeVariant variant = variants.get(i);
                tmpComposition.add(variant.component);
                nameBuilder.append(variant.suffix);
            }
        }
        return new Archetype(nameBuilder.toString(), tmpComposition.toArray(new Class[tmpComposition.size()]));
    }
}
