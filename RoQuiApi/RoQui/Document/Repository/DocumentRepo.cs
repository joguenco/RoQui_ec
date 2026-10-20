namespace RoQuiApi.RoQui.Document.Repository;

using RoQuiApi.Data;
using RoQuiApi.RoQui.Document.Model;

public class DocumentRepo : IDocumentRepo
{
    private readonly AppDbContext _context;

    public DocumentRepo(AppDbContext context)
    {
        _context = context;
    }

    public void CreateInvoice(Document invoice)
    {
        ArgumentNullException.ThrowIfNull(invoice);
        _context.Documents.Add(invoice);
    }

    public void DeleteDocument(Document document)
    {
        ArgumentNullException.ThrowIfNull(document);
        _context.Documents.Remove(document);
    }

    public Document? GetDocumentByCodeAndNumber(string code, string number)
    {
        return _context.Documents.FirstOrDefault(d => d.Code == code && d.Number == number);
    }

    public bool SaveChanges()
    {
        return _context.SaveChanges() >= 0;
    }
}