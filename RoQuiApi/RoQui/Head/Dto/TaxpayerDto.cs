using System.ComponentModel.DataAnnotations;

namespace RoQuiApi.RoQui.Head.Dto;

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

    public required virtual ICollection<EstablishmentDto> Establishments { get; set; }
}