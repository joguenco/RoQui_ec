namespace RoQuiApi.RoQui.Invoice.Model;

using System.ComponentModel.DataAnnotations.Schema;

[Table("documents_information")]
public class DocumentInformation
{
    [Column("id")]
    public int Id { get; set; }

    [Column("name", TypeName = "varchar")]
    public required string Name { get; set; }

    [Column("value", TypeName = "varchar")]
    public required string Value { get; set; }

    [Column("document_id")] public int DocumentId { get; set; }

    public required Document Document { get; set; }
}
