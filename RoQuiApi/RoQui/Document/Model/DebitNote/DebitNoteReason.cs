using System.ComponentModel.DataAnnotations.Schema;

namespace RoQuiApi.RoQui.Invoice.Model;

// El detalle de una nota de debito no son productos, son motivos: un texto y
// un valor. Por eso no cabe en documents_detail y va en su propia tabla.
[Table("debit_notes_reason")]
public class DebitNoteReason
{
    [Column("id")]
    public int Id { get; set; }

    [Column("line", TypeName = "int")]
    public required int Line { get; set; }

    [Column("reason", TypeName = "varchar")]
    public required string Reason { get; set; }

    [Column("value", TypeName = "decimal")]
    public required decimal Value { get; set; }

    [Column("document_id")]
    public int DocumentId { get; set; }
}
