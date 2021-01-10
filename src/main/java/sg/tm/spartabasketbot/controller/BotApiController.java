package sg.tm.spartabasketbot.controller;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.SimpleTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import sg.tm.spartabasketbot.dto.BotInfo;
import sg.tm.spartabasketbot.dto.StartCollectionResponse;
import sg.tm.spartabasketbot.service.IBotApiService;

/**
 * Контроллер обработки запросов пользователей
 */
@RestController
class BotApiController {

    @Autowired
    private IBotApiService botApiService;

    @GetMapping("/info")
    public BotInfo botInfo(){

        return BotInfo.builder()
            .name("SpartaBasketBot")
            .description("Это бот для облегчения сбора боллеров на трени на Спартаке.")
            .build();
    }

    @GetMapping("/training/collect")
    public StartCollectionResponse startCollection(){


        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone( ZoneId.of("UTC+7") );

        Instant instant = Instant.now();
        String output = formatter.format( instant );

        System.out.println(output);


        return StartCollectionResponse.builder()
            .build();
    }

    @PostMapping("/update")
    public BotApiMethod update(@RequestBody Update update){
        System.out.println("On update method: update = " + update);

        // сначала проверим и зарегистрируем пользователя
        return this.botApiService.recievedUpdate(update);
    }

/*    @GetMapping
    public List<ProductDTO.ResponseProduct.Basic> getProducts(
        @RequestParam(name = "name", required = false) String name,
        @Valid PageParams pageParams) {

        if (name == null) {
            List<Product> products = productService.getAviableProductsList(pageParams.getPage(),
                pageParams.getSize(),
                pageParams.getSortDir(),
                pageParams.getSort());
            return products.stream()
                .map(this::convertToProductBasicDto)
                .collect(Collectors.toList());
        } else {
            List<Product> products = productService.findAviableProductsByName(name,
                pageParams.getPage(),
                pageParams.getSize(),
                pageParams.getSortDir(),
                pageParams.getSort());
            return products.stream()
                .map(this::convertToProductBasicDto)
                .collect(Collectors.toList());
        }
    }*/


    /**
     * Получение доступного продукта по идентификатору
     */
/*    @GetMapping("{id}")
    ProductDTO.ResponseProduct.Public one(@PathVariable Long id) {

        Product product = productService.findAviableProductById(id);

        return convertToProductPublicDto(product);
    }*/

    /**
     * Запрос на создание нового продукта
     */
/*
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO.ResponseProduct.Public createProduct(
        @Valid @RequestBody Product newProduct) {

        return convertToProductPublicDto(productService.save(newProduct));
    }
*/


    /**
     * Создает от пользователя запрос на изменение продукта по идентификатору продукта.
     * ИДЕНТИФИКАТОР можно внести в тело запроса, но сделал так
     */
/*    @PutMapping("{id}")
    ProductDTO.ResponseProduct.Public addProductUpdate(@Valid @RequestBody Product updatedProduct, @PathVariable Long id) {

        return convertToProductPublicDto(productService.updateProduct(id, updatedProduct));
    }*/

    /**
     * функция конвертации сущности в нужный DTO
     */
/*
    private ProductDTO.ResponseProduct.Basic convertToProductBasicDto(Product product) {

        return modelMapper.map(product, ProductDTO.ResponseProduct.Basic.class);
    }
*/

    /**
     * функция конвертации сущности в нужный DTO
     */
/*    private ProductDTO.ResponseProduct.Public convertToProductPublicDto(Product product) {

        return modelMapper.map(product, ProductDTO.ResponseProduct.Public.class);
    }*/
}