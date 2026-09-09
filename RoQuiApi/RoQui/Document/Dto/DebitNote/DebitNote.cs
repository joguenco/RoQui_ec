namespace RoQuiApi.RoQui.Invoice.Dto;

using System.ComponentModel.DataAnnotations;
using RoQuiApi.RoQui.Document.Dto;

public class DebitNoteDto
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
    [Range(0, double.MaxValue, ErrorMessage = "TotalWithoutTaxes cannot be negative.")]
    public required decimal TotalWithoutTaxes { get; set; }

    [Required]
    [MinLength(1, ErrorMessage = "The debit note must contain at least one reason.")]
    public virtual required ICollection<DebitNoteDetailDto> DebitNoteDetails { get; set; }

    [Required]
    [MinLength(1, ErrorMessage = "The debit note must contain at least one tax.")]
    public virtual required ICollection<TaxDto> DebitNoteTaxes { get; set; }
}
