namespace RoQuiApi.RoQui.Shared;

using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

// Traduce la excepcion al codigo y al mensaje que toca. Antes todo salia como 500
// con el texto crudo de .NET, que no dice nada al cajero y ademas puede llevar
// rutas del servidor o nombres de tablas.
public static class ErrorResult
{
    public static ObjectResult From(Exception exception, string document)
    {
        // El detalle completo al log del servidor, nunca a la respuesta.
        Console.WriteLine($"Error en {document}: {exception}");

        return exception switch
        {
            // Si no se pudo guardar casi siempre es un dato del cliente: algo que no
            // cabe en la columna, un nulo donde no toca o un duplicado.
            DbUpdateException => Message("No se pudo guardar el documento, revise los datos", 400),

            _ => Message("Error interno del servidor", 500)
        };
    }

    private static ObjectResult Message(string title, int status)
    {
        // El title es el que lee roteg, asi que ahi va el motivo.
        return new ObjectResult(new MessageDto { Title = title, Status = status })
        {
            StatusCode = status
        };
    }
}
