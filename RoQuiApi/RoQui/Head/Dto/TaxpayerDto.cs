namespace RoQuiApi.RoQui.Head.Dto;

using System.ComponentModel.DataAnnotations;

public class TaxpayerDto
{
    [Required]
    [RegularExpression(@"^\d{13}$", ErrorMessage = "Identification must contain exactly 13 digits.")]
    public required string Identification { get; set; }

    [Required]
    public required string LegalName { get; set; }

    [Required]
    [RegularExpression(@"^(SI|NO)$", ErrorMessage = "ForcedAccounting must be either 'SI' or 'NO'")]
    public required string ForcedAccounting { get; set; }

    [MinLength(1, ErrorMessage = "SpecialTaxpayer cannot be empty.")]
    [RegularExpression(@"^\d{3,5}$", ErrorMessage = "SpecialTaxpayer must contain between 3 and 5 digits")]
    public string? SpecialTaxpayer { get; set; }

    [MinLength(1, ErrorMessage = "RetentionAgent cannot be empty.")]
    [RegularExpression(@"^1$", ErrorMessage = "RetentionAgent must be '1' or blank")]
    public string? RetentionAgent { get; set; }

    [MinLength(1, ErrorMessage = "Rimpe cannot be empty.")]
    [RegularExpression(@"^(CONTRIBUYENTE RÉGIMEN RIMPE|CONTRIBUYENTE NEGOCIO POPULAR - RÉGIMEN RIMPE)$", ErrorMessage = "Rimpe must be 'CONTRIBUYENTE RÉGIMEN RIMPE', 'CONTRIBUYENTE NEGOCIO POPULAR - RÉGIMEN RIMPE' or null.")]
    public string? Rimpe { get; set; }

    [Required]
    [Establishment]
    public virtual required ICollection<EstablishmentDto> Establishments { get; set; }
}