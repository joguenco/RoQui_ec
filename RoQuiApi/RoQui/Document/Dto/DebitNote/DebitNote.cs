namespace RoQuiApi.RoQui.Invoice.Dto;

using System.ComponentModel.DataAnnotations;
using RoQuiApi.RoQui.Shared;
using RoQuiApi.RoQui.Document.Dto;

public class DebitNoteDto : IValidatableObject
{
    [Required]
    public required string Code { get; set; }

    [Required]
    public required string Number { get; set; }

    [Required]
    public required DateTime Date { get; set; }

    [Required]
    [IdentificationType]
    public required string IdentificationType { get; set; }

    [Required]
    public required string Identification { get; set; }

    [Required]
    public required string LegalName { get; set; }

    [Required]
    [AccessKey]
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

    // El XML del SRI los admite opcionales, pero sin ellos el generador del PDF
    // se cae y el documento nunca llega a firmarse
    public virtual ICollection<PaymentDto>? Payments { get; set; }

    // La fecha, el codigo y el numero se usan para construir la clave de acceso,
    // asi que tienen que cuadrar con lo que viaja dentro de ella.
    public IEnumerable<ValidationResult> Validate(ValidationContext validationContext)
    {
        foreach (var error in RoQuiApi.RoQui.Shared.AccessKey.ValidateAgainst(AccessKey, Code, Number, Date, "05"))
        {
            yield return new ValidationResult(error);
        }
    }
}
