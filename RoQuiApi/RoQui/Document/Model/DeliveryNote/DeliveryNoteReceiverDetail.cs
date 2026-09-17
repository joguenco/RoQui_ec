using System.ComponentModel.DataAnnotations.Schema;

namespace RoQuiApi.RoQui.Invoice.Model;

[Table("delivery_notes_receiver_detail")]
public class DeliveryNoteReceiverDetail
{
    [Column("id")]
    public int Id { get; set; }

    [Column("line", TypeName = "int")]
    public required int Line { get; set; }

    [Column("principal_code", TypeName = "varchar")]
    public required string PrincipalCode { get; set; }

    [Column("name", TypeName = "varchar")]
    public required string Name { get; set; }

    [Column("quantity", TypeName = "decimal")]
    public required decimal Quantity { get; set; }

    [Column("delivery_note_receiver_id")]
    public int DeliveryNoteReceiverId { get; set; }

    public required DeliveryNoteReceiver DeliveryNoteReceiver { get; set; }
}
