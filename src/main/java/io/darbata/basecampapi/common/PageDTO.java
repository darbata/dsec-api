package io.darbata.basecampapi.common;

import java.util.List;

public record PageDTO<T> (
        List<T> content,
        String sortCol,
        boolean ascending,
        int pageSize,
        int pageNum
) { }
