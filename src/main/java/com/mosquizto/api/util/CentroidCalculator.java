package com.mosquizto.api.util;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CentroidCalculator {
    public float[] compute(List<float[]> vectors) {
        if (vectors.isEmpty()) throw new IllegalArgumentException("Empty vector list");

        int dim = vectors.get(0).length;
        float[] centroid = new float[dim];

        for (float[] vec : vectors) {
            for (int i = 0; i < dim; i++) {
                centroid[i] += vec[i];
            }
        }

        // Normalize
        float norm = 0f;
        for (int i = 0; i < dim; i++) {
            centroid[i] /= vectors.size();
            norm += centroid[i] * centroid[i];
        }
        norm = (float) Math.sqrt(norm);

        for (int i = 0; i < dim; i++) {
            centroid[i] /= norm;
        }

        return centroid;
    }
}
