namespace RoQuiApi.RoQui.Invoice.Repository;

using RoQuiApi.Data;
using RoQuiApi.RoQui.Invoice.Model;

public class InvoiceRepo : IInvoiceRepo
{
    private readonly AppDbContext _context;

    public InvoiceRepo(AppDbContext context)
    {
        _context = context;
    }

    public void CreateInvoice(Invoice invoice)
    {
        ArgumentNullException.ThrowIfNull(invoice);
        _context.Invoices.Add(invoice);
    }

    public bool SaveChanges()
    {
        return _context.SaveChanges() >= 0;
    }
}