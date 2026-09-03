using System.ComponentModel.DataAnnotations.Schema;

namespace RoQuiApi.RoQui.Invoice.Model;

[Table("invoices")]
public class Invoice
{
    [Column("id")]
    public int Id { get; set; }

    [Column("code", TypeName = "varchar")]
    public required string Code { get; set; }

    [Column("number", TypeName = "varchar")]
    public required string Number { get; set; }

    [Column("date", TypeName = "date")]
    public required DateTime Date { get; set; }

    [Column("identification_type", TypeName = "varchar")]
    public required string IdentificationType { get; set; }

    [Column("identification", TypeName = "varchar")]
    public required string Identification { get; set; }

    [Column("legal_name", TypeName = "varchar")]
    public required string LegalName { get; set; }

    [Column("address", TypeName = "varchar")]
    public required string Address { get; set; }

    [Column("delivery_note", TypeName = "varchar")]
    public string? DeliveryNote { get; set; }

    [Column("access_key", TypeName = "varchar")]
    public required string AccessKey { get; set; }

    [Column("created_at", TypeName = "timestamp with time zone")]
    public DateTime CreatedAt { get; set; }

    public virtual required ICollection<InvoiceDetail> InvoiceDetails { get; set; }
}