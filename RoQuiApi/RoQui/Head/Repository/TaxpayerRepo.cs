namespace RoQuiApi.RoQui.Head.Repository;

using Microsoft.EntityFrameworkCore;
using Data;
using Model;

public class TaxpayerRepo : ITaxpayerRepo
{
    private readonly AppDbContext _context;

    public TaxpayerRepo(AppDbContext context)
    {
        _context = context;
    }

    public int CountTaxpayers()
    {
        return _context.Taxpayers.Count();
    }

    public void CreateTaxpayer(Taxpayer taxpayer)
    {
        ArgumentNullException.ThrowIfNull(taxpayer);
        _context.Taxpayers.Add(taxpayer);
    }

    public Taxpayer? GetTaxpayerByIdentification(string identification)
    {
        return _context.Taxpayers
            .Include(t => t.Establishments)
            .FirstOrDefault(t => t.Identification == identification);
    }

    public void DeleteEstablishments(ICollection<Establishment> establishments)
    {
        ArgumentNullException.ThrowIfNull(establishments);
        _context.Establishments.RemoveRange(establishments);
    }

    public bool SaveChanges()
    {
        return _context.SaveChanges() >= 0;
    }
}
