// Función para mostrar la vista previa de la imagen seleccionada
function readURL(input) {
    console.log("Evento onchange activado");
    if (input.files && input.files[0]) {
        var lector = new FileReader();
        lector.onload = function (e) {
            console.log("Imagen cargada correctamente");
            // Actualizar la imagen de vista previa y mostrarla
            $('#imagePreview').attr('src', e.target.result).height(200).show();
        };
        lector.readAsDataURL(input.files[0]);
    }
}



