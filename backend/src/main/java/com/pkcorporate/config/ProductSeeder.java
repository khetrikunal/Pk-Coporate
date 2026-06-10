package com.pkcorporate.config;

import com.pkcorporate.entity.TShirtProduct;
import com.pkcorporate.repository.TShirtProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSeeder implements CommandLineRunner {

    private final TShirtProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() > 0) {
            log.info("Products table is already seeded. Skipping ProductSeeder.");
            return;
        }

        log.info("Seeding default T-shirt products in the database...");

        TShirtProduct tsh001 = TShirtProduct.builder()
                .productCode("TSH-001")
                .name("Classic Round Neck Tee")
                .description("Premium 180 GSM combed cotton, pre-shrunk, double-needle stitching for everyday corporate use.")
                .category("Round Neck")
                .fabricType("Cotton")
                .gsm("180 GSM")
                .neckType("Round Neck")
                .sleeveType("Half Sleeve")
                .minimumOrderQuantity(10)
                .basePrice(new BigDecimal("150.00"))
                .discountPrice(null)
                .stockQuantity(0)
                .brand("Generic")
                .availableColors(Arrays.asList("#FFFFFF", "#000000", "#1a1a2e", "#E10600", "#2563eb", "#16a34a", "#d97706"))
                .availableSizes(Arrays.asList("XS", "S", "M", "L", "XL", "2XL", "3XL"))
                .printTypes(Arrays.asList("Screen Print", "DTF", "Embroidery", "Sublimation"))
                .active(true)
                .build();

        TShirtProduct tsh002 = TShirtProduct.builder()
                .productCode("TSH-002")
                .name("Classic Polo Tee")
                .description("Corporate-grade pique fabric with 3-button placket and ribbed collar. Perfect for uniforms.")
                .category("Polo")
                .fabricType("Pique")
                .gsm("220 GSM")
                .neckType("Collar")
                .sleeveType("Half Sleeve")
                .minimumOrderQuantity(10)
                .basePrice(new BigDecimal("200.00"))
                .discountPrice(null)
                .stockQuantity(0)
                .brand("Generic")
                .availableColors(Arrays.asList("#FFFFFF", "#000000", "#1e3a5f", "#E10600", "#065f46"))
                .availableSizes(Arrays.asList("S", "M", "L", "XL", "2XL", "3XL"))
                .printTypes(Arrays.asList("Embroidery", "Screen Print", "Heat Transfer"))
                .active(true)
                .build();

        TShirtProduct tsh003 = TShirtProduct.builder()
                .productCode("TSH-003")
                .name("Classic Hoodie")
                .description("320 GSM fleece inner, kangaroo pocket, adjustable drawstring hood. Ideal for winter merch.")
                .category("Hoodie")
                .fabricType("Fleece")
                .gsm("320 GSM")
                .neckType("Hood")
                .sleeveType("Full Sleeve")
                .minimumOrderQuantity(10)
                .basePrice(new BigDecimal("250.00"))
                .discountPrice(null)
                .stockQuantity(0)
                .brand("Generic")
                .availableColors(Arrays.asList("#1a1a2e", "#000000", "#374151", "#E10600", "#7c3aed"))
                .availableSizes(Arrays.asList("S", "M", "L", "XL", "2XL", "3XL"))
                .printTypes(Arrays.asList("Screen Print", "DTF", "Embroidery"))
                .active(true)
                .build();

        TShirtProduct tsh004 = TShirtProduct.builder()
                .productCode("TSH-004")
                .name("V-Neck Premium Tee")
                .description("Lightweight 160 GSM combed cotton, perfect for summer corporate events and college wear.")
                .category("V-Neck")
                .fabricType("Cotton")
                .gsm("160 GSM")
                .neckType("V-Neck")
                .sleeveType("Half Sleeve")
                .minimumOrderQuantity(10)
                .basePrice(new BigDecimal("350.00"))
                .discountPrice(null)
                .stockQuantity(0)
                .brand("Generic")
                .availableColors(Arrays.asList("#FFFFFF", "#000000", "#E10600", "#6366f1", "#ec4899"))
                .availableSizes(Arrays.asList("XS", "S", "M", "L", "XL", "2XL"))
                .printTypes(Arrays.asList("DTF", "Sublimation", "Screen Print"))
                .active(true)
                .build();

        TShirtProduct tsh005 = TShirtProduct.builder()
                .productCode("TSH-005")
                .name("Heavyweight Cotton Tee")
                .description("60/40 cotton-poly blend for durability and comfort. Ideal for uniforms and year-round use.")
                .category("Round Neck")
                .fabricType("Cotton-Poly Blend")
                .gsm("200 GSM")
                .neckType("Round Neck")
                .sleeveType("Half Sleeve")
                .minimumOrderQuantity(10)
                .basePrice(new BigDecimal("450.00"))
                .discountPrice(null)
                .stockQuantity(0)
                .brand("Generic")
                .availableColors(Arrays.asList("#FFFFFF", "#000000", "#1e3a5f", "#374151", "#E10600"))
                .availableSizes(Arrays.asList("S", "M", "L", "XL", "2XL", "3XL"))
                .printTypes(Arrays.asList("Screen Print", "Embroidery", "DTF"))
                .active(true)
                .build();

        TShirtProduct tsh061 = TShirtProduct.builder()
                .productCode("TSH-061")
                .name("Dri-Fit Polo")
                .description("Moisture-wicking polyester for sports teams, marathons, and active corporate events.")
                .category("Polo")
                .fabricType("Polyester")
                .gsm("140 GSM")
                .neckType("Round Neck")
                .sleeveType("Half Sleeve")
                .minimumOrderQuantity(10)
                .basePrice(new BigDecimal("499.00"))
                .discountPrice(null)
                .stockQuantity(0)
                .brand("Generic")
                .availableColors(Arrays.asList("#FFFFFF", "#000000", "#E10600", "#0ea5e9", "#16a34a", "#f59e0b"))
                .availableSizes(Arrays.asList("XS", "S", "M", "L", "XL", "2XL"))
                .printTypes(Arrays.asList("Sublimation", "DTF", "Screen Print"))
                .active(true)
                .build();

        productRepository.saveAll(List.of(tsh001, tsh002, tsh003, tsh004, tsh005, tsh061));
        log.info("Default T-shirt products seeded successfully.");
    }
}
