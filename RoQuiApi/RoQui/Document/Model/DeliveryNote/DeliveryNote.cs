using System.ComponentModel.DataAnnotations.Schema;

namespace RoQuiApi.RoQui.Invoice.Model;

[Table("delivery_notes")]
public class DeliveryNote
{
    [Column("id")]
    public int Id { get; set; }

    [Column("code", TypeName = "varchar")]
    public required string Code { get; set; }

    [Column("number", TypeName = "varchar")]
    public required string Number { get; set; }

    [Column("date", TypeName = "date")]
    public required DateTime Date { get; set; }

    [Column("address_start", TypeName = "varchar")]
    public required string AddressStart { get; set; }

    [Column("carrier_identification_type", TypeName = "varchar")]
    public required string CarrierIdentificationType { get; set; }

    [Column("carrier_identification", TypeName = "varchar")]
    public required string CarrierIdentification { get; set; }

    [Column("carrier_legal_name", TypeName = "varchar")]
    public required string CarrierLegalName { get; set; }

    [Column("plate", TypeName = "varchar")]
    public required string Plate { get; set; }

    [Column("date_start_transport", TypeName = "date")]
    public required DateTime DateStartTransport { get; set; }

    [Column("date_end_transport", TypeName = "date")]
    public required DateTime DateEndTransport { get; set; }

    [Column("observation", TypeName = "varchar")]
    public string? Observation { get; set; }

    [Column("access_key", TypeName = "varchar")]
    public required string AccessKey { get; set; }

    [Column("created_at", TypeName = "timestamp without time zone")]
    public DateTime CreatedAt { get; set; } = DateTime.Now;

    public virtual required ICollection<DeliveryNoteReceiver> DeliveryNoteReceivers { get; set; }
}
