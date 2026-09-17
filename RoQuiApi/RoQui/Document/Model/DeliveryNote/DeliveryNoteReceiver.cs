using System.ComponentModel.DataAnnotations.Schema;

namespace RoQuiApi.RoQui.Invoice.Model;

[Table("delivery_notes_receiver")]
public class DeliveryNoteReceiver
{
    [Column("id")]
    public int Id { get; set; }

    [Column("line", TypeName = "int")]
    public required int Line { get; set; }

    [Column("identification_type", TypeName = "varchar")]
    public required string IdentificationType { get; set; }

    [Column("identification", TypeName = "varchar")]
    public required string Identification { get; set; }

    [Column("legal_name", TypeName = "varchar")]
    public required string LegalName { get; set; }

    [Column("address", TypeName = "varchar")]
    public required string Address { get; set; }

    [Column("transfer_reason", TypeName = "varchar")]
    public required string TransferReason { get; set; }

    [Column("code_document_support", TypeName = "varchar")]
    public string? CodeDocumentSupport { get; set; }

    [Column("number_document_support", TypeName = "varchar")]
    public string? NumberDocumentSupport { get; set; }

    [Column("authorization_document_support", TypeName = "varchar")]
    public string? AuthorizationDocumentSupport { get; set; }

    [Column("date_document_support", TypeName = "date")]
    public DateTime? DateDocumentSupport { get; set; }

    [Column("delivery_note_id")]
    public int DeliveryNoteId { get; set; }

    public required DeliveryNote DeliveryNote { get; set; }

    public virtual required ICollection<DeliveryNoteReceiverDetail> DeliveryNoteReceiverDetails { get; set; }
}
