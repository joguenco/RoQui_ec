using System.ComponentModel.DataAnnotations.Schema;

namespace RoQuiApi.RoQui.Invoice.Model;

[Table("documents_payment")]
public class DocumentPayment
{
    [Column("id")]
    public int Id { get; set; }

    [Column("code", TypeName = "varchar")]
    public required string Code { get; set; }

    [Column("total", TypeName = "decimal")]
    public required decimal Total { get; set; }

    [Column("deadline", TypeName = "decimal")]
    public decimal? Deadline { get; set; }

    [Column("unit_time", TypeName = "varchar")]
    public string? UnitTime { get; set; }

    [Column("document_id")]
    public int DocumentId { get; set; }

    public required Document Document { get; set; }
}
