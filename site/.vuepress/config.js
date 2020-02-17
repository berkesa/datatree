module.exports = {

	theme: '@vuepress/theme-default',
	title: 'DataTree Tools',
	description: 'Java Library for manipulating hierarchical data structures',
	base: '/datatree/',
	dest: '../docs',

    plugins: [
		[
			'@vuepress/back-to-top'
		],
        [
            '@vuepress/google-analytics', {'ga': 'UA-156080046-2'}
        ],
        [
            'robots',
            {
                host: "https://berkesa.github.io/datatree",
                disallowAll: false,
                allowAll: true,
                sitemap: "/sitemap.xml",
                policies: [
                    {
                        userAgent: '*'
                    }
                ]
            }
        ],
        [
            'sitemap',
            {
                hostname: 'https://berkesa.github.io/datatree',
                exclude: ['/404.html']
            }
        ]
    ],
	head: [
		['link', { rel: 'icon', href: 'favicon.ico'}]
	],
	themeConfig: {
		logo: 'logo.png',
		repo: 'https://github.com/berkesa/datatree',
		repoLabel: 'GitHub',
		docsRepo:  'datatree',
		docsDir:   'site',
		editLinks: true,
		editLinkText: 'Edit this page on GitHub',
		lastUpdated:  'Last Updated',		
		nav: [
			{ text: 'Home', link: '/' },
			{ text: 'Documentation', link: 'introduction' }
		],
		sidebar: [
			{
				title: 'DataTree Core API',
				sidebarDepth: 2,
				children: [
					['introduction', 'Introduction'],
					['manipulation', 'Data Manipulation']
				]
			},
			{
				title: 'Text-based formats',
				sidebarDepth: 2,
				children: [
					['format-json',       'JSON'],
					['format-xml',        'XML'],
					['format-yaml',       'YAML'],
					['format-toml',       'TOML'],
					['format-properties', 'Java Properties'],
					['format-csv',        'CSV'],
					['format-tsv',        'TSV']
				]
			},
			{
				title: 'Binary formats',
				sidebarDepth: 2,
				children: [
					['format-cbor',        'CBOR'],
					['format-bson',        'BSON'],
					['format-smile',       'SMILE'],
					['format-ion',         'Amazon ION'],
					['format-msgpack',     'MessagePack'],
					['format-java',        'Java Serialization'],
					['format-kryo',        'Kryo'],
					['performance-binary', 'Performance of binary APIs']
				]
			},
			{
				title: 'DataTree template engine',
				sidebarDepth: 2,
				children: [
					['template-introduction', 'Introduction'],
					['template-usage',        'Usage of the Engine'],
					['template-syntax',       'Syntax of templates']
				]
			},
			{
				title: 'Asynchronous data processing',
				sidebarDepth: 2,
				children: [
					['promise-introduction', 'Introduction'],
					['promise-usage',        'Usage of the Promises']
				]
			}
		]
	}
}