package com.app.godo.models;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "venues")
//@Setting(settingPath = "elasticsearch/serbian-analyzer.json")
public class VenueDocument {

    @Id
    private Long id;

//     @MultiField(
//             mainField = @Field(type = FieldType.Text, analyzer = "serbian_latin_cyrillic_analyzer", searchAnalyzer = "serbian_latin_cyrillic_analyzer"),
//             otherFields = {
//                     @InnerField(suffix = "keyword", type = FieldType.Keyword)
//             }
//     )
    @MultiField(
            mainField = @Field(type = FieldType.Text),
            otherFields = { @InnerField(suffix = "keyword", type = FieldType.Keyword) }
    )
    private String name;

//     @Field(type = FieldType.Text, analyzer = "serbian_latin_cyrillic_analyzer", searchAnalyzer = "serbian_latin_cyrillic_analyzer")
    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private String imageFilename;

    @Field(type = FieldType.Keyword)
    private String pdfFilename;

//     @Field(type = FieldType.Text, analyzer = "serbian_latin_cyrillic_analyzer", searchAnalyzer = "serbian_latin_cyrillic_analyzer")
    @Field(type = FieldType.Text)
    private String address;

    @Field(type = FieldType.Keyword)
    private String type;

//     @Field(type = FieldType.Text, analyzer = "serbian_latin_cyrillic_analyzer", searchAnalyzer = "serbian_latin_cyrillic_analyzer")
    @Field(type = FieldType.Text)
    private String pdfDescription;

    @Field(type = FieldType.Integer)
    private Integer reviewCount;

    @Field(type = FieldType.Double)
    private Double averageRating;

    @Field(type = FieldType.Double)
    private Double ratingPerformance;

    @Field(type = FieldType.Double)
    private Double ratingAmbient;

    @Field(type = FieldType.Double)
    private Double ratingVenue;

    @Field(type = FieldType.Double)
    private Double ratingOverallImpression;
}