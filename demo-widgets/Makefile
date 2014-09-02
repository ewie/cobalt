WIDGETS = $(shell ls -d1 */ | sed '/\./d;s%/$$%%')

.PHONY: widgets clean

widgets:
	for W in $(WIDGETS); do \
		cd $$W ; \
		zip -Z deflate ../$$W.wgt config.xml $$(cat config.xml | grep -P '<content\s+' | sed -E 's/\s*<content\s+src="([^"]+)".+/\1/') ; \
		cd - ; \
	done

clean:
	for W in $(WIDGETS); do \
		rm -f $$W.wgt ; \
	done
