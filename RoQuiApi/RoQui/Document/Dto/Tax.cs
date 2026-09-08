namespace RoQuiApi.RoQui.Invoice.Dto;

using System.ComponentModel.DataAnnotations;

public class TaxDto
{
    [Required]
    [RegularExpression(@"^[2356]$", ErrorMessage = "TaxCode must be 2 (IVA), 3 (ICE), 5 (IRBPNR) or 6 (IRBP).")]
    public required string TaxCode { get; set; }

    [Required]
    [RegularExpression(@"^\d{1,4}$", ErrorMessage = "TaxCodePercentage must contain between 1 and 4 digits.")]
    public required string TaxCodePercentage { get; set; }

    [Required]
    [Range(0, 100, ErrorMessage = "TaxValue must be a percentage between 0 and 100.")]
    public required decimal TaxValue { get; set; }

    [Required]
    [Range(0, double.MaxValue, ErrorMessage = "Base cannot be negative.")]
    public required decimal Base { get; set; }

    [Required]
    [Range(0, double.MaxValue, ErrorMessage = "Value cannot be negative.")]
    public required decimal Value { get; set; }
}
