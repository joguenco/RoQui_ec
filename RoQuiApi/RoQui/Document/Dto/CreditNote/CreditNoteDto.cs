namespace RoQuiApi.RoQui.Invoice.Dto;

using System.ComponentModel.DataAnnotations;

public class CreditNoteDto
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
    [RegularExpression(@"^\d{49}$", ErrorMessage = "The access key must contain exactly 49 digits.")]
    public required string AccessKey { get; set; }

    [Required]
    [RegularExpression(@"^\d{2}$", ErrorMessage = "ModifiedDocumentType must contain exactly 2 digits.")]
    public required string ModifiedDocumentType { get; set; }

    [Required]
    [StringLength(20, ErrorMessage = "ModifiedDocument cannot exceed 20 characters.")]
    public required string ModifiedDocument { get; set; }

    [Required]
    public required DateTime ModifiedDate { get; set; }

    [Required]
    [StringLength(300, ErrorMessage = "Reason cannot exceed 300 characters.")]
    public required string Reason { get; set; }

    [Required]
    [Range(0, double.MaxValue, ErrorMessage = "TotalWithoutTaxes cannot be negative.")]
    public required decimal TotalWithoutTaxes { get; set; }

    [Required]
    [Range(0, double.MaxValue, ErrorMessage = "ModifiedTotal cannot be negative.")]
    public required decimal ModifiedTotal { get; set; }

    [Required]
    [MinLength(1, ErrorMessage = "The credit note must contain at least one detail.")]
    public virtual required ICollection<CreditNoteDetailDto> CreditNoteDetails { get; set; }
}
