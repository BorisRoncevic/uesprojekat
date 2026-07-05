export interface VenueOverviewDto {
    id: number,
    name: string,
    description: string,
    address: string,
    averageRating: number,
    reviewCount?: number,
    ratingPerformance?: number,
    ratingAmbient?: number,
    ratingVenue?: number,
    ratingOverallImpression?: number,
    type: string,
    imagePath: string
    pdfPath: string
    highlight?: string
}
