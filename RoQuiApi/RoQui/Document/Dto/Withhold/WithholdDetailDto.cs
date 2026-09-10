namespace RoQuiApi.RoQui.Document.Withhold.Dto;

using System.ComponentModel.DataAnnotations;

public class WithholdDetailDto
{
    [Required]
    [Range(1, int.MaxValue, ErrorMessage = "Line must be greater than zero.")]
    public required int Line { get; set; }

    [Required]
    [RegularExpression(@"^[126]$", ErrorMessage = "TaxCode must be 1 (Renta), 2 (IVA) or 6 (ISD).")]
    public required string TaxCode { get; set; }

    [Required]
    [StringLength(10, ErrorMessage = "WithholdCode cannot exceed 10 characters.")]
    public required string WithholdCode { get; set; }

    [Required]
    [Range(0, double.MaxValue, ErrorMessage = "BaseValue cannot be negative.")]
    public required decimal BaseValue { get; set; }

    [Required]
    [Range(0, 100, ErrorMessage = "Percentage must be between 0 and 100.")]
    public required decimal Percentage { get; set; }

    [Required]
    [Range(0, double.MaxValue, ErrorMessage = "WithholdedValue cannot be negative.")]
    public required decimal WithholdedValue { get; set; }
}
