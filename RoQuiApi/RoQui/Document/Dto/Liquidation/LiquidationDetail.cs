namespace RoQuiApi.RoQui.Invoice.Dto;

using System.ComponentModel.DataAnnotations;

public class LiquidationDetailDto
{
    [Required]
    [Range(1, int.MaxValue, ErrorMessage = "Line must be greater than zero.")]
    public required int Line { get; set; }

    [Required]
    [StringLength(25, ErrorMessage = "ProductCode cannot exceed 25 characters.")]
    public required string ProductCode { get; set; }

    [Required]
    [StringLength(300, ErrorMessage = "ProductName cannot exceed 300 characters.")]
    public required string ProductName { get; set; }

    [Required]
    [Range(0, double.MaxValue, ErrorMessage = "Quantity cannot be negative.")]
    public required decimal Quantity { get; set; }

    public string? Unit { get; set; }

    [Required]
    [Range(0, double.MaxValue, ErrorMessage = "UnitPrice cannot be negative.")]
    public required decimal UnitPrice { get; set; }

    [Required]
    [RegularExpression(@"^\d{1,4}$", ErrorMessage = "TaxCode must contain between 1 and 4 digits.")]
    public required string TaxCode { get; set; }

    [Required]
    [Range(0, 100, ErrorMessage = "TaxIva must be a percentage between 0 and 100.")]
    public required decimal TaxIva { get; set; }

    [Required]
    [Range(0, double.MaxValue, ErrorMessage = "ValueIva cannot be negative.")]
    public required decimal ValueIva { get; set; }

    [Required]
    [Range(0, double.MaxValue, ErrorMessage = "Discount cannot be negative.")]
    public required decimal Discount { get; set; }

    [Required]
    [Range(0, double.MaxValue, ErrorMessage = "TotalPriceWithoutTax cannot be negative.")]
    public required decimal TotalPriceWithoutTax { get; set; }

    [Required]
    [MinLength(1, ErrorMessage = "The detail must contain at least one tax.")]
    public virtual required ICollection<TaxDto> LiquidationDetailTaxes { get; set; }
}
