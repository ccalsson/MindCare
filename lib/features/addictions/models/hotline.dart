class Hotline {
  final String name;
  final String? phone;
  final String? url;

  const Hotline({required this.name, this.phone, this.url});

  Map<String, dynamic> toMap() => {
        'name': name,
        if (phone != null) 'phone': phone,
        if (url != null) 'url': url,
      };

  factory Hotline.fromMap(Map<String, dynamic> map) {
    return Hotline(
      name: map['name'] as String,
      phone: map['phone'] as String?,
      url: map['url'] as String?,
    );
  }
}

