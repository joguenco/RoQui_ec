namespace RoQuiApi.RoQui.Invoice.Dto;

using System.ComponentModel.DataAnnotations;

public class InvoiceDto
{
    [Required]
    public required string Code { get; set; }
    [Required]
    public required string Number { get; set; }
    [Required]
    public required DateTime Date { get; set; }
    [Required]
    public required string IdentificationType { get; set; }
    [Required]
    public required string Identification { get; set; }
    [Required]
    public required string LegalName { get; set; }
    [Required]
    public required string Address { get; set; }

    public string? DeliveryNote { get; set; }
    [Required]
    [RegularExpression(@"^\d{49}$", ErrorMessage = "The access key must contain exactly 49 digits.")]
    public required string AccessKey { get; set; }
}