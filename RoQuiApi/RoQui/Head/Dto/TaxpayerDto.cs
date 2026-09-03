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
    [RegularExpression(@"^(SI|NO)$", ErrorMessage = "ForcedAccounting must be either 'SI' or 'NO'.")]
    public required string ForcedAccounting { get; set; }

    public string? SpecialTaxpayer { get; set; }
    public string? RetentionAgent { get; set; }
    public string? Rimpe { get; set; }

    public virtual required ICollection<EstablishmentDto> Establishments { get; set; }
}