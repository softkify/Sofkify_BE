package com.sofkify.productservice.application.port.in.command;

import java.math.BigDecimal;

public record CreateProductCommand(
   String name,
   String description,
   String sku,
   BigDecimal price,
   Integer stock
) {
}
