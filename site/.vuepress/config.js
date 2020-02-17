module.exports = {

	theme: '@vuepress/theme-default',
	title: 'DataTree API',
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
			'vuepress-plugin-medium-zoom',
			{
				selector: '.zoom',
				delay: 1000,
				options: {
					margin: 24,
					scrollOffset: 0
				}
			}
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
				title: 'Getting started',
				sidebarDepth: 2,
				children: [
					['introduction', 'Introduction']
				]
			}
		]
	}
}