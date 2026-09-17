namespace RoQuiApi.RoQui.Document.DeliveryNote.Dto;

using System.ComponentModel.DataAnnotations;

public class DeliveryNoteReceiverDto
{
    [Required]
    [Range(1, int.MaxValue, ErrorMessage = "Line must be greater than zero.")]
    public required int Line { get; set; }

    [Required]
    public required string IdentificationType { get; set; }

    [Required]
    public required string Identification { get; set; }

    [Required]
    public required string LegalName { get; set; }

    [Required]
    [StringLength(300, ErrorMessage = "Address cannot exceed 300 characters.")]
    public required string Address { get; set; }

    [Required]
    [StringLength(300, ErrorMessage = "TransferReason cannot exceed 300 characters.")]
    public required string TransferReason { get; set; }

    // El documento de sustento es opcional: no toda guia se emite contra una factura
    [RegularExpression(@"^\d{2}$", ErrorMessage = "CodeDocumentSupport must contain exactly 2 digits.")]
    public string? CodeDocumentSupport { get; set; }

    [StringLength(20, ErrorMessage = "NumberDocumentSupport cannot exceed 20 characters.")]
    public string? NumberDocumentSupport { get; set; }

    [RegularExpression(@"^\d{10}$|^\d{37}$|^\d{49}$", ErrorMessage = "AuthorizationDocumentSupport must contain 10, 37 or 49 digits.")]
    public string? AuthorizationDocumentSupport { get; set; }

    public DateTime? DateDocumentSupport { get; set; }

    [Required]
    [MinLength(1, ErrorMessage = "The receiver must contain at least one detail.")]
    public virtual required ICollection<DeliveryNoteReceiverDetailDto> DeliveryNoteReceiverDetails { get; set; }
}
